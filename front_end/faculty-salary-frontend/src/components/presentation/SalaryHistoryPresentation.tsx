import React, { useState } from 'react';
import { type SalaryHistoryDto, type FacultyProfileDto } from '../../model/model';
import moment from 'moment';
import { Container, Card, Table, Spinner, Alert, Button, Offcanvas, Navbar, Row, Col } from 'react-bootstrap';
import { FiDownload, FiLogOut } from 'react-icons/fi'; // Import icons
import  formatCurrency  from '../../utils/CurrencyFormat';


// 1. Define Props Interface
interface SalaryHistoryProps {
    history: SalaryHistoryDto[];
    profile: FacultyProfileDto | null; // Profile details
    loading: boolean;
    error: string | null;
    onDownload: (payslipId: number) => void;
    onLogout: () => void; // Logout handler
}

const SalaryHistoryPresentation: React.FC<SalaryHistoryProps> = ({ history, profile, loading, error, onDownload, onLogout }) => {
    
    // 2. Conditional Rendering for status checks
    if (loading) {
        return <Container className="text-center mt-5"><Spinner animation="border" variant="primary" /></Container>;
    }
    if (error) {
        return <Container className="mt-5"><Alert variant="danger">{error}</Alert></Container>;
    }
    
    // Get newest salary and profile details for display
    const currentSalary = history.length > 0 ? history[0] : null;
    const facultyName = profile ? profile.name : 'Faculty Member';
    const designation = profile ? profile.designation : 'Staff';
    // console.log('Profile Data:', profile);
    const [show, setShow] = useState(false);

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

    return (
      <div style={{ backgroundColor: '#E9E7E1' }}>
        <Navbar className="bg-body-tertiary  mb-4 shadow-sm p-3 rounded" style={{ backgroundColor: '#DADFE9' }}>
        <Container>
          <Row className="align-items-center w-100">
            <Col xs="auto">
              <Navbar.Brand href="#home">
                <img
                  src="https://www.iiitb.ac.in/includefiles/pages/icons/IIITB_logo1.png"
                  width="50"
                  height="50"
                  className="d-inline-block align-top me-2 badge badge-secondary"
                  alt="React Bootstrap logo"
                />  
              </Navbar.Brand>
              <Col>
               IIITB Faculty Salary Portal
              </Col>
            </Col>
            <Col className="text-center">
              <h4>Welcome, {facultyName}</h4>
            </Col>
            <Col xs="auto">
              <Button variant="success outline-primary"  onClick={handleShow}>
                My Profile
              </Button>
              <div className='bg-dark'>
              <Offcanvas show={show} onHide={handleClose} className='bg-light close'>
                <Offcanvas.Header className='offcanvas-header'closeButton>
                  <Offcanvas.Title className='text-lg'>My Details</Offcanvas.Title>
                 
                </Offcanvas.Header>
                <Offcanvas.Body className='offcanvas-body'>
                  
                  <Card className='shadow-sm border-0 bg-dark text-white'>
                  <Card.Body>
                    <div className='mb-3' style={{'height': '60px', 'width': '60px'}}>
                   <img src={profile?.profilePictureUrl} alt="Profile" className="rounded-circle me-3" style={{ width: '50px', height: '50px','float': 'left' }} />
                   </div>
                  <em><strong >Faculty Name: </strong></em>  {facultyName} <br/>
                   <em><strong >Internal ID: </strong></em>  {profile?.internal_id} <br/>
                  <em><strong >My Designation: </strong></em>  {designation} <br/>
                  <em><strong >Department:  </strong></em>  {designation.includes('Professor') ? (<>Belongs to both DSAI and CS<br/></>) : (<>Belongs to both CS<br/></>)}
                  <em><strong >Institute </strong></em>:  IIIT Bangalore <br/>
                  <em><strong >Email: </strong></em>  {profile?.email} <br/>
                  </Card.Body>
                  </Card>
                </Offcanvas.Body>
              </Offcanvas>
              </div>
            </Col>
            <Col xs="auto">
              <Button variant="outline-danger" onClick={onLogout}>
                <FiLogOut className="me-1" /> Logout
              </Button>
            </Col>
          </Row>
        </Container>
      </Navbar>
        <Container className="my-5 bg-sky-10 p-4 rounded shadow-sm">
            <div className="mb-4">
            {currentSalary && (
                <Card className="shadow-sm mb-5 border-success bg-transparent">
                   <Card.Body>
                    <Card.Title className="bg-success text-white p-3 mb-0">Your Current Salary</Card.Title>
                    <Card.Text className="p-3 ">
                        <strong>Last Payment:</strong> {moment(currentSalary.disbursementDate).format('MMMM Do YYYY')}<br/>
                        <strong>Gross Salary:</strong> {formatCurrency(currentSalary.grossSalary)}<br/>
                        <strong>Net Salary:</strong>  {formatCurrency(currentSalary.netSalary)}<br/>
                        <strong>Deductions:</strong>  {formatCurrency(currentSalary.deductions)}<br/>

                    </Card.Text>
                   </Card.Body>
                </Card>
            )}
    </div>
     <h3>Payment History</h3>
            <hr/>
           <div className="d-flex justify-content-between align-items-center mb-3">
           
            <Table striped bordered hover responsive className="mt-3 shadow-sm table-dark text-white">
                <thead className='font-mono'>
                    <tr className='table-row'>
                        <th className='text-info'>Month</th>
                        <th className='text-success'>Gross Salary paid</th>
                        <th className='text-danger'>Deductions</th>
                        <th className='text-info'>Net Salary paid</th>
                        <th className='text-info'>Download Pay Slip</th>
                    </tr>
                </thead>
                <tbody>
                    {history.map((record) => (
                        <tr className='table-row' key={record.id}>
                            <td>{moment(record.disbursementDate).format('YYYY-MM-DD')}</td>
                            {/* Gross Salary in INR */}
                            <td>{formatCurrency(record.grossSalary)}</td>
    
                        {/* Deductions in INR */}
                        <td className="text-danger table-cell">({formatCurrency(record.deductions)})</td>
    
                        {/* Net Salary in INR */}
                        <td className="fw-bold">{formatCurrency(record.netSalary)}</td>
                            <td>
                                {/* 3. Click handler calls the function passed via props */}
                                <Button 
                                    onClick={() => onDownload(record.id)} 
                                >
                                    <FiDownload />
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>
            </div>
        </Container>
        </div>
    );
};

export default SalaryHistoryPresentation;